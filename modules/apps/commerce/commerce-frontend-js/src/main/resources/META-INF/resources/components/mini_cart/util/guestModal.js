/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {COOKIE_TYPES, addParams, fetch, getCookie} from 'frontend-js-web';

import {
	ACCOUNT_ENTRY_TYPE_BUSINESS,
	ACCOUNT_ENTRY_TYPE_PERSON,
	SITE_TYPE_B2B,
	SITE_TYPE_B2C,
} from '../../../utilities/constants';
import CommerceCookie from '../../../utilities/cookies';
import {
	ACCOUNT_INFORMATION_COOKIE_IDENTIFIER,
	GUEST_COMMERCE_ORDER_COOKIE_IDENTIFIER,
	SUFFIX_IMMEDIATE_CHECKOUT,
} from '../../add_to_cart/constants';
import ForgotPasswordModalView from '../GuestSignInModalViews/ForgotPasswordModalView';
import SignInModalView from '../GuestSignInModalViews/SignInModalView';
import SignUpModalView from '../GuestSignInModalViews/SignUpModalView';

export const FORGOT_PASSWORD = 'forgot_password';
export const SIGN_IN = 'sign_in';
export const SIGN_UP = 'create_account';

export const INITIAL_VIEWS_MAP = {
	[FORGOT_PASSWORD]: {
		component: ForgotPasswordModalView,
		content: '',
		title: Liferay.Language.get('forgot-password'),
		url: '',
	},
	[SIGN_IN]: {
		component: SignInModalView,
		content: '',
		title: Liferay.Language.get('sign-in-to-checkout'),
		url: '',
	},
	[SIGN_UP]: {
		component: SignUpModalView,
		content: '',
		title: Liferay.Language.get('sign-up'),
		url: '',
	},
};

export async function fetchHTML(url) {
	try {
		const response = await fetch(url);

		return response.text();
	}
	catch (error) {
		if (process.env.NODE_ENV === 'development') {
			console.error(error);
		}

		return '';
	}
}

export function getAccountTypes() {
	const {commerceSiteType} = Liferay.CommerceContext;

	const ACCOUNT_TYPE_BUSINESS = {
		label: Liferay.Language.get('business'),
		value: ACCOUNT_ENTRY_TYPE_BUSINESS,
	};
	const ACCOUNT_TYPE_PERSON = {
		label: Liferay.Language.get('person'),
		value: ACCOUNT_ENTRY_TYPE_PERSON,
	};

	if (commerceSiteType === SITE_TYPE_B2B) {
		return [ACCOUNT_TYPE_BUSINESS];
	}
	else if (commerceSiteType === SITE_TYPE_B2C) {
		return [ACCOUNT_TYPE_PERSON];
	}

	return [ACCOUNT_TYPE_BUSINESS, ACCOUNT_TYPE_PERSON];
}

export function getIframeDOMHooks(iframeElement, modalView) {
	if (modalView === FORGOT_PASSWORD) {
		return setupForgotPassword(iframeElement);
	}
	else if (modalView === SIGN_UP) {
		return setupSignUp(iframeElement);
	}
}

export function resizeIframeHeight(iframeElement, sourceElement) {
	iframeElement.style.height = `${sourceElement.scrollHeight}px`;
}

function setupFromSignIn(signInViewHTML, signInURL) {
	const signInContainer = window.document.createElement('div');

	signInContainer.innerHTML = signInViewHTML;

	const navigationElement = signInContainer.querySelector('.navigation');

	const anchorElements = Array.from(
		navigationElement.querySelectorAll('.navigation a')
	);

	let forgotPasswordAnchor = null;

	const views = anchorElements.reduce(
		(views, element) => {
			if (element.href.includes(FORGOT_PASSWORD)) {
				forgotPasswordAnchor = element;
				views[FORGOT_PASSWORD].url = toPopUp(element.href);

				element.href = '#';
			}
			else if (element.href.includes(SIGN_UP)) {
				views[SIGN_UP].url = toPopUp(element.href);

				element.href = '#';
			}

			return views;
		},
		{
			[FORGOT_PASSWORD]: INITIAL_VIEWS_MAP[FORGOT_PASSWORD],
			[SIGN_UP]: INITIAL_VIEWS_MAP[SIGN_UP],
		}
	);

	if (forgotPasswordAnchor) {
		forgotPasswordAnchor.classList.add('mb-3', 'small');
		forgotPasswordAnchor.firstElementChild.dataset.linkId = FORGOT_PASSWORD;

		const passwordInputElement = signInContainer.querySelector(
			'input[type="password"]'
		);

		passwordInputElement.classList.add('mb-1');
		passwordInputElement.parentElement.appendChild(forgotPasswordAnchor);

		navigationElement.remove();
	}

	const signInButton = signInContainer.querySelector('button[type="submit"]');

	signInButton.classList.add('btn-block');

	const formContent = signInContainer.querySelector('#Content .panel-body');

	formContent.classList.remove('panel-body');

	return {
		...views,
		[SIGN_IN]: {
			...INITIAL_VIEWS_MAP[SIGN_IN],
			content: signInContainer.innerHTML,
			url: signInURL,
		},
	};
}

function setupForgotPassword(iframeElement) {
	const iframeBody = iframeElement?.contentDocument.body;

	const buttonHolderElement = iframeBody.querySelector('.button-holder');
	const formElement = iframeBody.querySelector('form');
	const loginContainer = iframeBody.querySelector('.login-container');
	const navigationElement = iframeBody.querySelector('.navigation');
	const portletBody = iframeBody.querySelector('.portlet-body');

	const panelBody = formElement.querySelector('.panel-body');

	buttonHolderElement.classList.add('hide');
	navigationElement.remove();
	portletBody.classList.add('px-1', 'overflow-hidden');
	loginContainer.classList.add('p-0');
	panelBody.classList.add('p-0');
	resizeIframeHeight(iframeElement, formElement);

	return {
		form: formElement,
		submitButton: buttonHolderElement.querySelector('button'),
	};
}

function setupSignUp(iframeElement) {
	const iframeBody = iframeElement?.contentDocument.body;

	const buttonHolderElement = iframeBody.querySelector('.button-holder');
	const formElement = iframeBody.querySelector('form');
	const navigationElement = iframeBody.querySelector('.navigation');
	const portletBody = iframeBody.querySelector('.portlet-body');

	const sheetElement = formElement.querySelector('form .sheet');
	const sheetSectionElement = formElement.querySelector('.sheet-section');

	const optionalFieldElements = sheetSectionElement.querySelectorAll(
		'.control-label:not(:has(.reference-mark))'
	);

	Array.from(optionalFieldElements).forEach((element) => {
		if (!element.parentElement.className.includes('input-Date-wrapper')) {
			element.parentElement.remove();
		}
	});

	navigationElement.remove();
	sheetElement.classList.remove('sheet', 'sheet-lg');
	portletBody.classList.add('px-1', 'overflow-hidden');
	buttonHolderElement.classList.add('hide');
	resizeIframeHeight(iframeElement, formElement);

	return {
		form: formElement,
		submitButton: buttonHolderElement.querySelector('button'),
	};
}

export async function setupViewsMap(signInURL) {
	signInURL = toPopUp(signInURL);

	const signInViewHTML = await fetchHTML(signInURL);

	return setupFromSignIn(signInViewHTML, signInURL);
}

export function storeAccountInformation({accountName, accountType, userEmail}) {
	const {commerceChannelGroupId: groupId} = Liferay.CommerceContext;

	const cookie = new CommerceCookie(ACCOUNT_INFORMATION_COOKIE_IDENTIFIER);

	cookie.setValue(
		groupId,
		`accountEntryName=${accountName}#accountEntryType=${accountType}#userEmailAddress=${userEmail}`
	);
}

export function storeImmediateCheckout() {
	const {commerceChannelGroupId: groupId} = Liferay.CommerceContext;

	const cookieKey = `${GUEST_COMMERCE_ORDER_COOKIE_IDENTIFIER}${groupId}`;

	const cookieValue = getCookie(cookieKey, COOKIE_TYPES.NECESSARY);

	const cookie = new CommerceCookie(GUEST_COMMERCE_ORDER_COOKIE_IDENTIFIER);

	cookie.setValue(groupId, `${cookieValue}${SUFFIX_IMMEDIATE_CHECKOUT}`);
}

function toPopUp(url) {
	url = url.replace('maximized', 'pop_up').replace('exclusive', 'pop_up');

	return addParams('windowState=pop_up', url);
}
