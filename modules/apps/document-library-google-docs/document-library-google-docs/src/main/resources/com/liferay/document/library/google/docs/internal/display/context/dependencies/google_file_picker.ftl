function GoogleFilePicker(callback) {
	if (window.google) {
		callback();
	}
	else {
		Liferay.once('gisAPILoaded', callback);
	}
}

GoogleFilePicker.prototype = {
	constructor: GoogleFilePicker,

	openPicker: function() {
		var instance = this;

		instance._createPicker();

		if (!instance._authAPILoaded) {
			gapi.load(
				'auth',
				{
					'callback': instance._onAuthAPILoad.bind(instance)
				}
			);
		}

		if (!instance._pickerAPILoaded) {
			gapi.load(
				'picker',
				{
					'callback': instance._onPickerAPILoad.bind(instance)
				}
			);
		}
	},

	_createPicker: function() {
		var instance = this;

		if (instance._pickerAPILoaded && instance._authAPILoaded) {
			var viewId = google.picker.ViewId;

			var groupDocuments = new google.picker.ViewGroup(viewId.DOCS);

			groupDocuments.addView(viewId.DOCUMENTS);
			groupDocuments.addView(viewId.SPREADSHEETS);
			groupDocuments.addView(viewId.PRESENTATIONS);

			var picker = new google.picker.PickerBuilder();

			picker.addViewGroup(groupDocuments);

			picker.addView(viewId.RECENTLY_PICKED);

			picker.setOAuthToken(instance._oauthToken);
			picker.setDeveloperKey(GoogleFilePicker.API_KEY);
			picker.setCallback(instance._pickerCallback);

			picker.build().setVisible(true);
		}
	},

	_onAuthAPILoad: function() {
		var instance = this;

		var tokenClient = google.accounts.oauth2.initTokenClient({
			callback: (authResult) => {
				if (authResult && authResult.error !== undefined) {
					throw (authResult);
				}

				instance._oauthToken = authResult.access_token;

				instance._authAPILoaded = true;

				instance._createPicker();
			},
			client_id: GoogleFilePicker.CLIENT_ID,
			error_callback: function(error) {
				if (
					typeof process !== 'undefined' &&
					process.env.NODE_ENV === 'development'
				) {
					console.error(error);
				}
			},
			immediate: false,
			scope: GoogleFilePicker.SCOPE
		});

		tokenClient.requestAccessToken();
	},

	_onPickerAPILoad: function() {
		var instance = this;

		instance._pickerAPILoaded = true;

		instance._createPicker();
	},

	_pickerCallback: function(data) {
		if (data[google.picker.Response.ACTION] === google.picker.Action.PICKED) {
			var doc = data[google.picker.Response.DOCUMENTS][0];

			var googlePickerDoc = google.picker.Document;

			${onFilePickCallback}(
				{
					"DESCRIPTION": doc[googlePickerDoc.DESCRIPTION] || '',
					"EMBEDDABLE_URL": doc[googlePickerDoc.EMBEDDABLE_URL] || '',
					"ICON_URL": doc[googlePickerDoc.ICON_URL] || '',
					"ID": doc[googlePickerDoc.ID],
					"NAME": doc[googlePickerDoc.NAME],
					"TITLE": doc[googlePickerDoc.NAME].replace(/\.[^.]*$/, ''),
					"URL": doc[googlePickerDoc.URL] || ''
				});
		}
	}
};

GoogleFilePicker.API_KEY = '${htmlUtil.escapeJS(googleAppsAPIKey)}';

GoogleFilePicker.CLIENT_ID = '${htmlUtil.escapeJS(googleClientId)}';

GoogleFilePicker.SCOPE = 'https://www.googleapis.com/auth/drive.readonly';

window.onGisAPILoad = function() {
	Liferay.fire('gisAPILoaded');
};

if (!window.gapi && !document.getElementById('googleAPILoader')) {
	var scriptNode = document.createElement('script');

	scriptNode.id = 'googleAPILoader';
	scriptNode.src = 'https://apis.google.com/js/api.js?onload=onGoogleAPILoad';

	document.body.appendChild(scriptNode);
}

if (!window.google) {
	var scriptNodeGis = document.createElement('script');

	scriptNodeGis.id = 'gisAPILoader';
	scriptNodeGis.onload = onGisAPILoad;
	scriptNodeGis.src = 'https://accounts.google.com/gsi/client';

	document.body.appendChild(scriptNodeGis);
}

var FilePicker = GoogleFilePicker;