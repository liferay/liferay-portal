/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {isTrackable} from '../utils/assets';
import {onReady} from '../utils/events';

/**
 * Returns an identifier for a form element.
 */
function getFormKey(form: AnalyticsType.HTMLElement & {action?: string}) {
	return (form.dataset.analyticsAssetId ||
		form.id ||
		form.getAttribute('name') ||
		form.action) as string;
}

/**
 * Returns analytics payload with field information.
 */
function getFieldPayload({
	form,
	name,
}: {
	form: AnalyticsType.HTMLElement;
	name: string;
}) {
	return {
		fieldName: name,
		...getFormPayload(form),
	};
}

/**
 * Returns analytics payload with form information.
 */
function getFormPayload(form: AnalyticsType.HTMLElement) {
	const payload = {
		formId: getFormKey(form).trim(),
	};

	if (form.dataset.analyticsAssetTitle) {
		Object.assign(payload, {
			title: form.dataset.analyticsAssetTitle.trim(),
		});
	}

	if (form.dataset.analyticsExternalReferenceCode) {
		Object.assign(payload, {
			externalReferenceCode:
				form.dataset.analyticsExternalReferenceCode.trim(),
		});
	}

	return payload;
}

/**
 * Wether a form is trackable or not.
 */
function isTrackableForm(element: AnalyticsType.HTMLElement) {
	return isTrackable(element) && !!getFormKey(element);
}

/**
 * Adds an event listener for the blur event and sends analytics information
 */
function trackFieldBlurred(analytics: Analytics) {
	const onBlur = (event: MouseEvent) => {
		const target = event.target as unknown as {
			form: AnalyticsType.HTMLElement;
			name: string;
		};
		const {form} = target;

		if (!form || !isTrackableForm(form)) {
			return;
		}

		const payload = getFieldPayload(target);

		const blurMark = `${payload.formId}${payload.fieldName}blurred`;
		performance.mark(blurMark);

		const focusMark = `${payload.formId}${payload.fieldName}focused`;
		performance.measure('focusDuration', focusMark, blurMark);

		const perfData = performance.getEntriesByName('focusDuration').pop();

		const focusDuration =
			perfData && typeof perfData.duration === 'number'
				? ~~perfData.duration
				: 0;

		Object.assign(payload, {focusDuration});

		analytics.send(
			AnalyticsType.EventId.FieldBlurred,
			AnalyticsType.ApplicationId.Form,
			payload
		);

		performance.clearMarks('focusDuration');
	};

	document.addEventListener('blur', onBlur as EventListener, true);

	return () =>
		document.removeEventListener('blur', onBlur as EventListener, true);
}

/**
 * Adds an event listener for the focus event and sends analytics information
 */
function trackFieldFocused(analytics: Analytics) {
	const onFocus = (event: MouseEvent) => {
		const target = event.target as unknown as {
			form: AnalyticsType.HTMLElement;
			name: string;
		};
		const {form} = target;

		if (!form || !isTrackableForm(form)) {
			return;
		}

		const payload = getFieldPayload(target);

		const focusMark = `${payload.formId}${payload.fieldName}focused`;
		performance.mark(focusMark);

		analytics.send(
			AnalyticsType.EventId.FieldFocused,
			AnalyticsType.ApplicationId.Form,
			payload
		);
	};

	document.addEventListener('focus', onFocus as EventListener, true);

	return () =>
		document.removeEventListener('focus', onFocus as EventListener, true);
}

/**
 * Adds an event listener for a form submission and sends information when that
 */
function trackFormSubmitted(analytics: Analytics) {
	const onSubmit = (event: MouseEvent) => {
		const target = event.target as AnalyticsType.HTMLElement;

		if (
			!isTrackableForm(target) ||
			(isTrackableForm(target) && event.defaultPrevented)
		) {
			return;
		}

		analytics.send(
			AnalyticsType.EventId.FormSubmitted,
			AnalyticsType.ApplicationId.Form,
			getFormPayload(target)
		);
	};

	document.addEventListener('submit', onSubmit as EventListener, true);

	return () =>
		document.removeEventListener('submit', onSubmit as EventListener, true);
}

/**
 * Sends information about forms rendered on the page when it was loaded.
 */
function trackFormViewed(analytics: Analytics) {
	return onReady(() => {
		Array.prototype.slice
			.call(document.querySelectorAll('form'))
			.filter((form) => isTrackableForm(form))
			.forEach((form) => {
				const payload = getFormPayload(form);

				analytics.send(
					AnalyticsType.EventId.FormViewed,
					AnalyticsType.ApplicationId.Form,
					payload
				);
			});
	});
}

/**
 * Plugin function that registers listener against form events
 */
function forms(analytics: Analytics) {
	const stopTrackingFieldBlurred = trackFieldBlurred(analytics);
	const stopTrackingFieldFocused = trackFieldFocused(analytics);
	const stopTrackingFormSubmitted = trackFormSubmitted(analytics);
	const stopTrackingFormViewed = trackFormViewed(analytics);

	return () => {
		stopTrackingFieldBlurred();
		stopTrackingFieldFocused();
		stopTrackingFormSubmitted();
		stopTrackingFormViewed();
	};
}

export {forms};
export default forms;
