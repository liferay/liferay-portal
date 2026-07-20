import {EventNames} from 'shared/util/constants';

/**
 * eventId for each (applicationId, action), mirroring analytics-client-js
 * Analytics.EventId. Action keys are the WEB_BEHAVIORS names (EventNames).
 */
const EVENT_ID_MAP: {
	[applicationId: string]: {[action: string]: string};
} = {
	Blog: {
		click: 'blogClicked',
		comment: 'commentPosted',
		impression: 'blogImpressionMade',
		view: 'blogViewed',
	},
	Document: {
		download: 'documentDownloaded',
		impression: 'documentImpressionMade',
		view: 'documentPreviewed',
	},
	Form: {
		submit: 'formSubmitted',
		view: 'formViewed',
	},
	ObjectEntry: {
		download: 'objectEntryDownloaded',
		impression: 'objectEntryImpressionMade',
		view: 'objectEntryViewed',
	},
	WebContent: {
		click: 'webContentClicked',
		impression: 'webContentImpressionMade',
		view: 'webContentViewed',
	},
};

/**
 * The analytics eventId for a given (applicationId, action). Pages are always
 * tracked as pageViewed.
 */
export const getEventId = (
	applicationId: string,
	action: string | undefined
): string =>
	(action && EVENT_ID_MAP[applicationId]?.[action]) ||
	(applicationId === 'Page' ? 'pageViewed' : '');

/**
 * Inverse of EVENT_ID_MAP (plus pageViewed): maps a stored, application-specific
 * eventId back to its generic WEB_BEHAVIORS action. Every "viewed" variant
 * collapses to `view`, "downloaded" to `download`, and so on.
 */
const EVENT_ID_TO_ACTION: {[eventId: string]: string} = {
	pageViewed: EventNames.View,
	...Object.values(EVENT_ID_MAP).reduce(
		(acc, actionMap) => {
			Object.entries(actionMap).forEach(([action, eventId]) => {
				acc[eventId] = action;
			});

			return acc;
		},
		{} as {[eventId: string]: string}
	),
};

/**
 * Resolves a stored activityKey eventId back to the generic action used as the
 * WEB_BEHAVIORS name. Falls back to the eventId itself so legacy activityKeys
 * whose eventId already equals the action still resolve.
 */
export const getActionFromEventId = (
	eventId: string | undefined
): string | undefined =>
	eventId ? EVENT_ID_TO_ACTION[eventId] ?? eventId : eventId;
