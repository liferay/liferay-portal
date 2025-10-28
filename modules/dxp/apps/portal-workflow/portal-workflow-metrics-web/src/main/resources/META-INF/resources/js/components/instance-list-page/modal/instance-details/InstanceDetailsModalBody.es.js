/* eslint-disable react-hooks/exhaustive-deps */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayModal, {useModal} from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {WorkflowInstanceTracker} from '@liferay/portal-workflow-instance-tracker-web';
import React, {useContext, useState} from 'react';

import ContentView from '../../../../shared/components/content-view/ContentView.es';
import RetryButton from '../../../../shared/components/list/RetryButton.es';
import {remainingTimeFormat} from '../../../../shared/util/duration.es';
import moment from '../../../../shared/util/moment.es';
import {AppContext} from '../../../AppContext.es';

function Body({
	assetTitle,
	assetType,
	assignees = [{name: Liferay.Language.get('unassigned')}],
	completed,
	creator,
	dateCompletion,
	dateCreated,
	setRetry,
	id,
	slaResults = [],
	taskNames = [],
}) {
	const {baseResourceURL} = useContext(AppContext);
	const SLAs = {notStarted: [], open: [], resolved: []};

	slaResults.forEach((result) => {
		let slaGroup = '';

		switch (result.status) {
			case 'NEW': {
				slaGroup = 'notStarted';
				break;
			}
			case 'STOPPED': {
				slaGroup = 'resolved';
				break;
			}
			default: {
				slaGroup = 'open';
			}
		}

		SLAs[slaGroup].push(result);
	});

	const statesProps = {
		errorProps: {
			actionButton: (
				<RetryButton onClick={() => setRetry((retry) => retry + 1)} />
			),
			className: 'py-8',
			hideAnimation: true,
			message: Liferay.Language.get('unable-to-retrieve-data'),
			messageClassName: 'small',
		},
		loadingProps: {className: 'py-8'},
	};

	const [showInstanceTrackerModal, setShowInstanceTrackerModal] =
		useState(false);

	const {observer} = useModal({
		onClose: () => {
			setShowInstanceTrackerModal(false);
		},
	});

	return (
		<>
			<ClayModal.Body>
				<ContentView {...statesProps}>
					<Body.SectionTitle>
						{Liferay.Language.get('due-date-by-sla')}
					</Body.SectionTitle>

					{!slaResults.length && (
						<p>
							<span className="font-weight-medium text-muted">
								{Liferay.Language.get(
									'no-sla-records-for-this-item'
								)}
							</span>
						</p>
					)}

					{!!SLAs.open.length && (
						<Body.SectionSubTitle>
							{`${Liferay.Language.get('open').toUpperCase()} (${
								SLAs.open.length
							})`}
						</Body.SectionSubTitle>
					)}

					{SLAs.open.map((item) => (
						<Body.SLAResultItem key={item.id} {...item} />
					))}

					{!!SLAs.resolved.length && (
						<Body.SectionSubTitle>
							{`${Liferay.Language.get(
								'resolved'
							).toUpperCase()} (${SLAs.resolved.length})`}
						</Body.SectionSubTitle>
					)}

					{SLAs.resolved.map((item) => (
						<Body.SLAResultItem key={item.id} {...item} />
					))}

					{!!SLAs.notStarted.length && (
						<Body.SectionSubTitle>
							{`${Liferay.Language.get(
								'not-started'
							).toUpperCase()} (${SLAs.notStarted.length})`}
						</Body.SectionSubTitle>
					)}

					{SLAs.notStarted.map((item) => (
						<Body.SLAResultItem key={item.id} {...item} />
					))}

					<Body.SectionTitle className="mt-5">
						{Liferay.Language.get('process-details')}
					</Body.SectionTitle>

					<Body.SectionAttribute
						description={Liferay.Language.get('process-status')}
						detail={
							<>
								{completed
									? Liferay.Language.get('completed')
									: Liferay.Language.get('pending')}

								<ClayTooltipProvider>
									<ClayLink
										className="ml-1 tracker-tooltip"
										data-tooltip-align="top"
										onClick={() =>
											setShowInstanceTrackerModal(true)
										}
										title={Liferay.Language.get(
											'click-and-see'
										)}
									>
										(
										{Liferay.Language.get('track-workflow')}
										)
									</ClayLink>
								</ClayTooltipProvider>
							</>
						}
					/>

					<Body.SectionAttribute
						description={Liferay.Language.get('created-by')}
						detail={creator ? creator.name : ''}
					/>

					{dateCreated && (
						<Body.SectionAttribute
							description={Liferay.Language.get('creation-date')}
							detail={moment(dateCreated).format(
								Liferay.Language.get('mmm-dd-yyyy-lt')
							)}
						/>
					)}

					<Body.SectionAttribute
						description={Liferay.Language.get('asset-type')}
						detail={assetType}
					/>

					<Body.SectionAttribute
						description={Liferay.Language.get('asset-title')}
						detail={assetTitle}
					/>

					{!completed && (
						<Body.SectionAttribute
							description={Liferay.Language.get('current-step')}
							detail={taskNames.join(', ')}
						/>
					)}

					{completed && dateCompletion && (
						<Body.SectionAttribute
							description={Liferay.Language.get('end-date')}
							detail={moment(dateCompletion).format(
								Liferay.Language.get('mmm-dd-yyyy-lt')
							)}
						/>
					)}

					{!completed && (
						<Body.SectionAttribute
							description={Liferay.Language.get(
								'current-assignee'
							)}
							detail={assignees[0].name}
						/>
					)}

					<ClayButton
						className="mb-1 mt-3"
						data-tooltip-align="bottom"
						data-tooltip-delay="0"
						displayType="secondary"
						onClick={() =>
							window.open(
								`/group/control_panel/manage/-/workflow_instance/view/${id}`,
								'_blank'
							)
						}
						small
						title={Liferay.Language.get('open-page-in-a-new-tab')}
					>
						{Liferay.Language.get('go-to-submission-page')}

						<span className="inline-item inline-item-after">
							<ClayIcon symbol="shortcut" />
						</span>
					</ClayButton>
				</ContentView>
			</ClayModal.Body>

			{showInstanceTrackerModal && (
				<ClayModal observer={observer} size="full-screen">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('track-workflow')}
					</ClayModal.Header>

					<ClayModal.Body>
						<WorkflowInstanceTracker
							baseResourceURL={baseResourceURL}
							workflowInstanceId={id}
						/>
					</ClayModal.Body>
				</ClayModal>
			)}
		</>
	);
}

function SectionTitle({children, className = ''}) {
	const classNames = `${className} font-weight-medium h4 mb-4`;

	return <div className={classNames}>{children}</div>;
}

function SectionSubTitle({children}) {
	return (
		<div className="font-weight-medium h5 mb-4 mt-4 text-secondary">
			{children}
		</div>
	);
}

function SectionAttribute({description, detail}) {
	return (
		<ClayLayout.Row containerElement="p">
			<ClayLayout.Col
				className="font-weight-medium small text-secondary"
				containerElement="span"
				size="2"
			>
				{`${description}`}
			</ClayLayout.Col>

			<ClayLayout.Col className="small" containerElement="span">
				{detail}
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
}

function getResultItemInfo({onTime, status}) {
	if (status === 'NEW') {
		return {bgColor: 'text-info', iconName: 'hr'};
	}

	if (onTime) {
		return {bgColor: 'success', iconName: 'check-circle'};
	}

	return {bgColor: 'danger', iconName: 'exclamation-circle'};
}

function SLAResultItem({dateOverdue, name, onTime, remainingTime, status}) {
	const {bgColor, iconName} = getResultItemInfo({onTime, status});

	const getStatusText = (status) => {
		switch (status) {
			case 'NEW': {
				return `(${Liferay.Language.get('untracked')})`;
			}
			case 'PAUSED': {
				return `(${Liferay.Language.get('sla-paused')})`;
			}
			case 'RUNNING': {
				const [durationText, onTimeText] = remainingTimeFormat(
					onTime,
					remainingTime
				);

				return `${moment(dateOverdue).format(
					Liferay.Language.get('mmm-dd-yyyy-lt')
				)} (${durationText} ${onTimeText})`;
			}
			default: {
				if (status === 'STOPPED' && onTime) {
					return `(${Liferay.Language.get('resolved-on-time')})`;
				}

				return `(${Liferay.Language.get('resolved-overdue')})`;
			}
		}
	};

	return (
		<div className="sla-result">
			<span className={`bg-${bgColor}-light inline-item sticker`}>
				<ClayIcon className={`text-${bgColor}`} symbol={iconName} />
			</span>

			<span className="font-weight-medium small text-secondary">
				{`${name}` + ' '}
			</span>

			<span className="small">{getStatusText(status)}</span>
		</div>
	);
}

Body.SLAResultItem = SLAResultItem;
Body.SectionTitle = SectionTitle;
Body.SectionSubTitle = SectionSubTitle;
Body.SectionAttribute = SectionAttribute;

export default Body;
