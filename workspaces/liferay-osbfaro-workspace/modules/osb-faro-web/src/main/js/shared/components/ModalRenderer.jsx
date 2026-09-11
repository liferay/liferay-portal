import AddChannelModal from './modals/AddChannelModal';
import autobind from 'autobind-decorator';
import BatchActionModal from './modals/BatchActionModal';
import ConfirmationModal from './modals/ConfirmationModal';
import ConnectDXPModal from './modals/ConnectDXPModal';
import ContactSalesModal from './modals/ContactSalesModal';
import CreateMappingModal from './modals/CreateMappingModal';
import CSVPreviewModal from './modals/CSVPreviewModal';
import DeleteChannelModal from './modals/DeleteChannelModal';
import DeleteConfirmationModal from './modals/DeleteConfirmationModal';
import EditAttributeEventModal from './modals/EditAttributeEventModal';
import EditEmailReportsModal from './modals/EditEmailReportsModal';
import ExportLogModal from './modals/ExportLogModal';
import FieldPreviewModal from './modals/FieldPreviewModal';
import HelpWidgetModal from './modals/help-widget-modal';
import IndividualAttributesModal from './modals/IndividualAttributesModal';
import InputModal from './modals/InputModal';
import InterestTopicModal from './modals/InterestTopicsModal';
import InviteUsersModal from './modals/InviteUsersModal';
import LoadingModal from './modals/LoadingModal';
import ManageLifecycleNotificationsModal from './modals/ManageLifecycleNotificationsModal';
import ManageSegmentNotificationsModal from './modals/ManageSegmentNotificationsModal';
import ManuallyRetrainModelModal from './modals/ManuallyRetrainModelModal';
import MatchingPagesModal from './modals/MatchingPagesModal';
import NewRequestModal from './modals/NewRequestModal';
import NewRuleModal from './modals/NewRuleModal';
import OnboardingModal from './modals/onboarding-modal';
import React from 'react';
import SearchableEntitiesTableModal from './modals/SearchableEntitiesTableModal';
import SearchableTableModal from './modals/SearchableTableModal';
import SearchableTableModalGraphql from './modals/SearchableTableModalGraphql';
import SelectChannelsModal from './modals/SelectChannelsModal';
import SelectItemsModal from './modals/SelectItemsModal';
import TestModal from './modals/TestModal';
import TimeZoneSelectionModal from './modals/TimeZoneSelectionModal';
import UnableDeletePropertyModal from './modals/UnableDeletePropertyModal';
import UnassignedSegmentsModal from './modals/unassigned-segments-modal';
import {close, modalTypes} from '../actions/modals';
import {connect} from 'react-redux';
import {List} from 'immutable';
import {onEnter} from 'shared/util/key-constants';
import {PropTypes} from 'prop-types';

const BODY_CLASSNAME = 'modal-open';

const COMPONENT_MAP = {
	[modalTypes.ADD_CHANNEL_MODAL]: AddChannelModal,
	[modalTypes.BATCH_ACTION_MODAL]: BatchActionModal,
	[modalTypes.CONFIRMATION_MODAL]: ConfirmationModal,
	[modalTypes.CONNECT_DXP_MODAL]: ConnectDXPModal,
	[modalTypes.CONTACT_SALES_MODAL]: ContactSalesModal,
	[modalTypes.CREATE_MAPPING_MODAL]: CreateMappingModal,
	[modalTypes.CSV_PREVIEW_MODAL]: CSVPreviewModal,
	[modalTypes.DELETE_CHANNEL_MODAL]: DeleteChannelModal,
	[modalTypes.DELETE_CONFIRMATION_MODAL]: DeleteConfirmationModal,
	[modalTypes.EDIT_ATTRIBUTE_EVENT_MODAL]: EditAttributeEventModal,
	[modalTypes.EDIT_EMAIL_REPORTS]: EditEmailReportsModal,
	[modalTypes.EXPORT_LOG_MODAL]: ExportLogModal,
	[modalTypes.FIELD_PREVIEW_MODAL]: FieldPreviewModal,
	[modalTypes.HELP_WIDGET_MODAL]: HelpWidgetModal,
	[modalTypes.INDIVIDUAL_ATTRIBUTES_MODAL]: IndividualAttributesModal,
	[modalTypes.INPUT_MODAL]: InputModal,
	[modalTypes.INSERT_BLOCKED_KEYWORDS]: InterestTopicModal,
	[modalTypes.INVITE_USERS_MODAL]: InviteUsersModal,
	[modalTypes.LOADING_MODAL]: LoadingModal,
	[modalTypes.MANAGE_LIFECYCLE_NOTIFICATIONS_MODAL]: ManageLifecycleNotificationsModal,
	[modalTypes.MANAGE_SEGMENT_NOTIFICATIONS_MODAL]: ManageSegmentNotificationsModal,
	[modalTypes.MANUALLY_RETRAIN_MODEL_MODAL]: ManuallyRetrainModelModal,
	[modalTypes.MATCHING_PAGES_MODAL]: MatchingPagesModal,
	[modalTypes.NEW_REQUEST_MODAL]: NewRequestModal,
	[modalTypes.NEW_RULE_MODAL]: NewRuleModal,
	[modalTypes.ONBOARDING_MODAL]: OnboardingModal,
	[modalTypes.UNASSIGNED_SEGMENTS_MODAL]: UnassignedSegmentsModal,
	[modalTypes.SEARCHABLE_ENTITIES_TABLE_MODAL]: SearchableEntitiesTableModal,
	[modalTypes.SEARCHABLE_TABLE_MODAL]: SearchableTableModal,
	[modalTypes.SEARCHABLE_TABLE_MODAL_GRAPHQL]: SearchableTableModalGraphql,
	[modalTypes.SELECT_ITEMS_MODAL]: SelectItemsModal,
	[modalTypes.SELECT_CHANNELS_MODAL]: SelectChannelsModal,
	[modalTypes.TEST]: TestModal,
	[modalTypes.TIME_ZONE_SELECTION_MODAL]: TimeZoneSelectionModal,
	[modalTypes.UNABLE_DELETE_PROPERTY_MODAL]: UnableDeletePropertyModal
};

function toggleBodyModalOpen(open = true) {
	if (open) {
		document.body.classList.add(BODY_CLASSNAME);
	} else {
		document.body.classList.remove(BODY_CLASSNAME);
	}
}

export class ModalRenderer extends React.Component {
	static propTypes = {
		close: PropTypes.func.isRequired,
		modalsIList: PropTypes.instanceOf(List).isRequired
	};

	componentDidUpdate() {
		toggleBodyModalOpen(!!this.getCurrentModal());
	}

	componentWillUnmount() {
		toggleBodyModalOpen(false);
	}

	getCurrentModal() {
		return this.props.modalsIList.last();
	}

	@autobind
	@onEnter
	handleKeyPress(event) {
		this.handleClickOutside(event);
	}

	@autobind
	handleClickOutside(event) {
		const currentModalIMap = this.getCurrentModal();

		if (
			currentModalIMap.get('closeOnBlur', true) &&
			event.target.matches('.modal-container')
		) {
			this.props.close();
		}
	}

	render() {
		return (
			<div
				className={`modal-renderer-root${
					this.props.className ? ` ${this.props.className}` : ''
				}`}
			>
				{this.props.modalsIList
					.map((modalIMap, i) => {
						const ModalComponent =
							COMPONENT_MAP[modalIMap.get('type')];

						return (
							<div
								className={`modal-container d-block fade modal show${
									this.props.className
										? ` ${this.props.className}`
										: ''
								}`}
								key={i}
								onClick={this.handleClickOutside}
								onKeyPress={this.handleKeyPress}
								role='button'
								tabIndex='0'
							>
								<ModalComponent
									{...modalIMap.get('props').toObject()}
								/>
							</div>
						);
					})
					.toJS()}

				{!!this.getCurrentModal() && (
					<div className='modal-backdrop fade show' />
				)}
			</div>
		);
	}
}

export default connect(
	state => ({
		modalsIList: state.get('modals', new List())
	}),
	{close}
)(ModalRenderer);
