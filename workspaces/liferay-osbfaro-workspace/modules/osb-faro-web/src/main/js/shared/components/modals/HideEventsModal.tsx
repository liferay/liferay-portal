import ConfirmationModal from 'shared/components/ConfirmationModal';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {Alert} from 'shared/types';
import {sub} from 'shared/util/lang';

interface IHideableEvent {
	hidden?: boolean;
	id: string;
}

interface IHideEventsModalProps<T extends IHideableEvent> {
	addAlert: (alert: {
		alertType: any;
		message: string;
		timeout?: boolean;
	}) => any;
	eventName: (event: T) => string | undefined;
	events: T[];
	hideEvents: (eventDefinitionIds: string[]) => Promise<unknown>;
	observer: Observer;
	onClose: () => void;
	singleEventHiddenMessage?: (eventName?: string) => string;
}

const HideEventsModal = <T extends IHideableEvent>({
	addAlert,
	eventName,
	events,
	hideEvents,
	observer,
	onClose,
	singleEventHiddenMessage = (name) =>
		sub(Liferay.Language.get('x-has-been-set-to-hide'), [name]) as string,
}: IHideEventsModalProps<T>) => {
	const visibleEvents = events.filter(({hidden}) => !hidden);

	const visibleEventsCount = visibleEvents.length;

	return (
		<ConfirmationModal
			message={
				<p className="text-secondary">
					{Liferay.Language.get(
						'hiding-events-in-the-interface-may-require-reconfiguration-of-segments-and-other-analysis-using-this-event.-hidden-events-will-be-available-for-calculating-metrics'
					)}
				</p>
			}
			observer={observer}
			onClose={onClose}
			onSubmit={() => {
				hideEvents(events.map(({id}) => id))
					.then(() => {
						addAlert({
							alertType: Alert.Types.Success,
							message:
								visibleEventsCount > 1
									? (sub(
											Liferay.Language.get(
												'x-events-have-been-set-to-hide'
											),
											[visibleEventsCount]
										) as string)
									: singleEventHiddenMessage(
											eventName(visibleEvents[0])
										),
						});
					})
					.catch(() =>
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get(
								'there-was-an-error-processing-your-request.-please-try-again'
							),
							timeout: false,
						})
					);
			}}
			submitButtonDisplay="warning"
			submitMessage={Liferay.Language.get('hide')}
			title={
				visibleEventsCount > 1
					? Liferay.Language.get('hide-events')
					: sub(Liferay.Language.get('hide-x'), [
							eventName(visibleEvents[0]),
						])
			}
			type="warning"
		/>
	);
};

export default HideEventsModal;
