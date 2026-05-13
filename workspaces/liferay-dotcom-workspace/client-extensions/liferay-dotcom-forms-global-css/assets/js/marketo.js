// eslint-disable-next-line no-unused-vars

function loadMarketoForm(
	formEl,
	formId,
	submitButtonText,
	submitButtonColor,
	redirectUrl,
	assetInfo,
	utmInfo
) {
	var mktoFormConfig = {
		munchkinId: '212-DQY-814',
		podId: '//pages.liferay.com'
	};

	loadMarketoFormsJs(mktoFormConfig, function() {
		formEl.id = 'mktoForm_' + formId;

		MktoForms2.loadForm(
			mktoFormConfig.podId,
			mktoFormConfig.munchkinId,
			formId,
			function(form) {
				formEl.id = '';

				// eslint-disable-next-line es5/no-es6-methods
				var buttonElem = form.getFormElem().find('button.mktoButton');

				if (buttonElem) {
					buttonElem.html(submitButtonText);

					if (submitButtonColor) {
						buttonElem.addClass('bg-' + submitButtonColor);
					}
				}

				var skeletonWrapperEl = formEl.querySelector(
					'.marketo-skeleton-wrapper'
				);

				if (skeletonWrapperEl) {
					skeletonWrapperEl.classList.toggle('hide-skeleton');
				}

				namespaceFormElements(form);
				removeFormStyles(form);
				initializeFocusedFilledClasses();

				if (assetInfo && assetInfo.title && assetInfo.asset_url) {
					form.setValues({
						Recent_Asset_Name__c: assetInfo.title,
						Recent_Asset_URL__c: assetInfo.asset_url
					});
				}

				if (utmInfo) {
					form.setValues({
						Last_CID__c: utmInfo.utm_cid,
						Last_Campaign__c: utmInfo.utm_campaign,
						Last_Content__c: utmInfo.utm_content,
						Last_Medium__c: utmInfo.utm_medium,
						Last_Source__c: utmInfo.utm_source,
						Last_Term__c: utmInfo.utm_term
					});
				}

				var formInitEvent = new CustomEvent('marketoFormInit', {
					detail: form
				});
				document.dispatchEvent(formInitEvent);

				sendFormViewAnalytics(formId, assetInfo);

				var formElement = form.getFormElem()[0];

				formElement.addEventListener('submit', function(event) {
					window.SixSenseEvent = event;
					// eslint-disable-next-line no-console
					console.info('6sense: Form submit initiated.');
				});

				formElement
					.getElementsByTagName('button')[0]
					.setAttribute('name', 'mktoButton_' + form.getId());

				formElement.setAttribute('name', 'mktoForm_' + form.getId());

				form.onSuccess(function() {
					// eslint-disable-next-line no-undef
					if (window._6si) {
						// eslint-disable-next-line no-undef
						window._6si.send(window.SixSenseEvent);

						// eslint-disable-next-line no-console
						console.info('6sense: Form submit completed.');
					} else {
						console.warn(
							'6sense: Event object not found. Form fill not logged.'
						);
					}

					sendFormSubmitAnalytics(formId, assetInfo);
					location.href = redirectUrl;

					return false;
				});
			}
		);

		MktoForms2.onFormRender(function(form) {
			namespaceFormElements(form);
			removeFormStyles(form);
		});

		MktoForms2.whenReady(function(form) {
			form.onValidate(function(nativeValid) {
				if (!nativeValid) {
					return;
				}

				var formEl = form.getFormElem()[0],
					currentValues = form.getValues(),
					buttonEl = formEl.querySelector('.mktoButtonWrap');

				var bannedEmailFieldNames = [
					'Email',
					'Email_Address_2__c'
				].filter(function(fieldName) {
					return isEmailBanned(currentValues[fieldName]);
				});

				buttonEl.removeAttribute('data-error-message');

				var allEmailsValid = bannedEmailFieldNames.length == 0;

				if (!allEmailsValid) {
					form.showErrorMessage(
						Liferay.Language.get('email-validate-message'),
						MktoForms2.$(
							formEl.querySelector(
								"[name='" + bannedEmailFieldNames[0] + "']"
							)
						)
					);
				}

				form.submittable(allEmailsValid);
			});

			function isEmailBanned(emailAddress) {
				return [
					/@gmail\./,
					/@yahoo\./,
					/@hotmail\./,
					/@live\./,
					/@aol\./,
					/@outlook\./,
					/@mailinator\./,
					/@info\./,
					/@xgh6\./,
					/@business\./,
					/@bsuiness\./,
					/@gnnmail\./,
					/@gma\./,
					/@1122\./,
					/@hot\./,
					/@h05d\./,
					/@work\./,
					/@mailin\./,
					/@mail\./,
					/@mainator\./,
					/@service\./,
					/@domai\./,
					/@domain\./,
					/@armyspy\./,
					/@maiator\./,
					/@company\./,
					/@temporary\./
				].some(function(pattern) {
					return new RegExp(
						pattern.source + '[a-z0-9-.]+$',
						'i'
					).test(emailAddress);
				});
			}
		});
	});
}

var loadMarketoFormsJs = (function() {
	var loading = false;

	return function(mktoFormConfig, callback) {
		if (!loading) {
			loading = true;

			if (typeof MktoForms2 !== 'undefined') {
				callback();

				return;
			}

			var mktoForms2BaseStyleSpan = document.createElement('span');
			mktoForms2BaseStyleSpan.id = 'mktoForms2BaseStyle';
			mktoForms2BaseStyleSpan.setAttribute('class', 'd-none');

			document.head.appendChild(mktoForms2BaseStyleSpan);

			var mktoForms2ThemeStyleSpan = document.createElement('span');
			mktoForms2ThemeStyleSpan.id = 'mktoForms2ThemeStyle';
			mktoForms2ThemeStyleSpan.setAttribute('class', 'd-none');

			document.head.appendChild(mktoForms2ThemeStyleSpan);

			var mktoStyleLoadedDiv = document.createElement('div');
			mktoStyleLoadedDiv.id = 'mktoStyleLoaded';

			// Prevent marketo from loading its own styles by adding the test styles it creates

			document.head.appendChild(mktoStyleLoadedDiv);

			var mktoStyle = document.createElement('style');
			mktoStyle.appendChild(
				document.createTextNode(
					'#mktoStyleLoaded { display: none; background-color: #123456; border-top-color: #123456; color: #123456;}'
				)
			);
			document.head.appendChild(mktoStyle);

			var script = document.createElement('script');

			if (script) {
				script.setAttribute(
					'src',
					mktoFormConfig.podId + '/js/forms2/js/forms2.min.js'
				);
				document.head.appendChild(script);
			}
		}

		waitForMarketoAPI(callback);
	};
})();

function waitForMarketoAPI(callback) {
	var timeout = 10;

	var poll = function() {
		setTimeout(function() {
			timeout--;
			if (typeof MktoForms2 !== 'undefined') {
				callback();
			} else if (timeout > 0) {
				poll();
			}
		}, 100);
	};

	poll();
}

function namespaceFormElements(form) {
	var formEl = form.getFormElem()[0];

	var randomNamespace = '_' + new Date().getTime() + Math.random();

	formEl.querySelectorAll('label[for]').forEach(function(labelEl) {
		var forEl = labelEl.control;

		var fieldWrapper = labelEl.parentNode;

		var radioListEl = fieldWrapper.querySelector(
			'#' + labelEl.id + ' ~ .mktoRadioList'
		);
		var checkboxListEl = fieldWrapper.querySelector(
			'#' + labelEl.id + ' ~ .mktoCheckboxList'
		);

		if (forEl) {
			labelEl.htmlFor = forEl.id = forEl.id + randomNamespace;
		}

		if (
			checkboxListEl &&
			!fieldWrapper.classList.contains('checkbox-field')
		) {
			fieldWrapper.classList.add('checkbox-field');

			var checkboxListInputs = checkboxListEl.querySelectorAll('input');

			if (
				!fieldWrapper.classList.contains('single-checkbox') &&
				checkboxListInputs.length === 1
			) {
				fieldWrapper.classList.add('single-checkbox');
			}
		} else if (
			radioListEl &&
			!fieldWrapper.classList.contains('radio-field')
		) {
			fieldWrapper.classList.add('radio-field');
		}
	});
}

function removeFormStyles(form) {
	var formEl = form.getFormElem()[0];

	var arrayify = getSelection.call.bind([].slice);

	var styledEls = arrayify(formEl.querySelectorAll('[style]')).concat(formEl);

	styledEls.forEach(function(el) {
		el.removeAttribute('style');
	});

	formEl.querySelectorAll('style').forEach(function(el) {
		el.remove();
	});
}

// eslint-disable-next-line no-unused-vars
function removeMarketoStylesheets(form) {
	var formEl = form.getFormElem()[0];

	var arrayify = getSelection.call.bind([].slice);

	var styleSheets = arrayify(document.styleSheets);

	styleSheets.forEach(function(stylesheet) {
		if (
			// eslint-disable-next-line no-undef
			[mktoForms2BaseStyle, mktoForms2ThemeStyle].indexOf(
				stylesheet.ownerNode
			) != -1 ||
			formEl.contains(stylesheet.ownerNode)
		) {
			stylesheet.disabled = true;
		}
	});
}

function initializeFocusedFilledClasses() {
	var formFields = document.querySelectorAll(
		'.mktoFormRow input, .mktoFormRow textarea, .mktoFormRow select'
	);

	var j = formFields.length;
	while (j--) {
		var formField = formFields.item(j);

		toggleFilledClass(null, formField);
		formField.addEventListener('change', checkErrorClass, true);
		formField.addEventListener('focus', checkErrorClass, true);
		formField.addEventListener('keydown', checkErrorClass, true);
		formField.addEventListener('change', toggleFilledClass, true);
		formField.addEventListener('focus', toggleFocusedClass, true);
		formField.addEventListener(
			'blur',
			function(event, target) {
				checkErrorClass(event, target);

				toggleFocusedClass(event, target);
			},
			true
		);
	}
}

function toggleFilledClass(event, target) {
	target = target ? target : event.target;

	var wrapper = target.parentNode;

	if (target.value) {
		wrapper.classList.add('filled');
	} else {
		wrapper.classList.remove('filled');
	}
}

function toggleFocusedClass(event) {
	event.target.parentNode.classList.toggle('focused');
}

function checkErrorClass(event, target) {
	target = target ? target : event.target;

	if (target.classList.contains('mktoInvalid')) {
		target.parentNode.classList.add('error');
	} else {
		target.parentNode.classList.remove('error');
	}
}

function sendFormViewAnalytics(formId, assetInfo) {
	if (window.Analytics) {
		Analytics.send('formViewed', 'Form', {
			formId: formId,
			title: document.title
		});

		if (assetInfo) {
			Analytics.send('documentPreviewed', 'Document', {
				fileEntryId: assetInfo.fileEntryId,
				fileEntryUUID: assetInfo.fileEntryUUID,
				groupId: assetInfo.groupId,
				title: assetInfo.title,
				version: assetInfo.version
			});
		}
	}
}

function sendFormSubmitAnalytics(formId, assetInfo) {
	if (window.Analytics) {
		Analytics.send('formSubmitted', 'Form', {
			formId: formId
		});

		if (assetInfo) {
			Analytics.send('documentDownloaded', 'Document', {
				fileEntryId: assetInfo.fileEntryId,
				groupId: assetInfo.groupId,
				title: assetInfo.title,
				version: assetInfo.version
			});
		}
	}
}

if (typeof module !== 'undefined') {
	module.exports = loadMarketoForm;
}
