/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import propsTransformer from '../../../src/main/resources/META-INF/resources/js/admin/SaveConfigurationButtonPropsTransformer';

describe('SaveConfigurationButtonPropsTransformer', () => {
	let form;

	beforeEach(() => {
		document.body.innerHTML = `
			<form id="_ns_fm">
				<input id="_ns_emailKBArticleAddedBody" name="_ns_preferences--emailKBArticleAddedBody--" type="hidden" value="Body" />
			</form>
		`;

		form = document.getElementById('_ns_fm');

		window.submitForm = jest.fn();
	});

	afterEach(() => {
		delete window._ns_emailKBArticleAdded;
		delete window.submitForm;

		document.body.innerHTML = '';
	});

	function save() {
		propsTransformer({
			additionalProps: {emailParam: 'emailKBArticleAdded'},
			portletNamespace: '_ns_',
		}).onClick();
	}

	it('copies the editor content into the body input before submitting', () => {
		window._ns_emailKBArticleAdded = {getHTML: () => '<p>New body</p>'};

		save();

		expect(
			document.getElementById('_ns_emailKBArticleAddedBody')
		).toHaveValue('<p>New body</p>');
		expect(window.submitForm).toHaveBeenCalledWith(form);
	});

	it('submits the form when the editor writes its own form input', () => {
		save();

		expect(
			document.getElementById('_ns_emailKBArticleAddedBody')
		).toHaveValue('Body');
		expect(window.submitForm).toHaveBeenCalledWith(form);
	});

	it('submits the form when there is no email editor', () => {
		propsTransformer({
			additionalProps: {},
			portletNamespace: '_ns_',
		}).onClick();

		expect(window.submitForm).toHaveBeenCalledWith(form);
	});
});
