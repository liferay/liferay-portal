AUI().use(
	function(A) {
		Liferay.namespace('Patcher');

		Liferay.Patcher = {
			closeWindow: function() {
				var dialog = Liferay.Util.getWindow();

				if (dialog) {
					dialog.hide();
				}
			},
			compareTicket: function(a, b) {
				var aParts = a.split('-');
				var bParts = b.split('-');

				if (aParts[0] != bParts[0]) {
					return aParts[0] > bParts[0] ? 1 : -1;
				}

				if ((aParts.length == 1) || (bParts.length == 1)) {
					return bParts.length - aParts.length;
				}

				return parseInt(aParts[1]) - parseInt(bParts[1]);
			},
			getTicketLink: function(className, ticket, title) {
				if (ticket.toUpperCase() != ticket) {
					return ticket;
				}

				var ticketURL = 'https://liferay.atlassian.net/browse/' + ticket;

				if (className) {
					var productVersionElement = querySelector('patcherProductVersionId');
					var productVersionId = productVersionElement.value;
					var projectVersionElement = querySelector('patcherProjectVersionId');
					var projectVersionId = projectVersionElement.value;

					var params = {
						advancedSearch: true,
						andOperator: true,
						hideOldFixVersions: true,
						patcherFixName: ticket,
						patcherProductVersionId: productVersionId,
						patcherProjectVersionIdFilter: projectVersionId
					};

					ticketURL = 'https://patcher.liferay.com/group/guest/patching/-/osb_patcher?' + getQueryString(params);
				}

				if (!title) {
					title = ticket;
				}

				return '<a class="nowrap ' + className + '" href="' + ticketURL + '" title="' + title + '" target="_blank">' + ticket + '</a>';
			},
			getTicketLinks: function(text) {
				return text.split(',').map(function(x) { return x.trim(); }).sort(Liferay.Patcher.compareTicket).map(Liferay.Patcher.getTicketLink.bind(null, '')).join(', ');
			},
			getTicketLinksPopover: function(Y, align_points, tickets, trigger) {
				var popover = new Y.Popover(
					{
						align: {
							node: trigger,
							points: align_points
						},
						headerContent: 'JIRA Links',
						bodyContent: Liferay.Patcher.getTicketLinks(tickets.value),
						position: 'right',
						visible: false,
						zIndex: 1
					}
				).render();

				trigger.on(
					'click',
					function() {
						popover.set('visible', !popover.get('visible'));
						popover.set('bodyContent', Liferay.Patcher.getTicketLinks(tickets.value));
					}
				);

				trigger.on(
					'change',
					function() {
						popover.set('bodyContent', Liferay.Patcher.getTicketLinks(tickets.value));
					}
				);
			},
			openWindow: function(url, title, modal, width) {
				Liferay.Util.openWindow(
					{
						dialog: {
							align: Liferay.Util.Window.ALIGN_CENTER,
							modal: modal,
							width: width
						},
						title: title,
						uri: url
					}
				);
			},
			populateProjectVersionField: function(productVersionId, select, map) {
				while (select.firstChild) {
					select.removeChild(select.firstChild);
				}

				if (productVersionId && (productVersionId != 0)) {
					var projectVersions = map[productVersionId];

					for (var i = 0; i < projectVersions.length; i++) {
						if (!projectVersions[i].hide) {
							var option = document.createElement('option');

							option.innerHTML = projectVersions[i].name;

							option.value = projectVersions[i].patcherProjectVersionId;

							select.appendChild(option);
						}
					}
				}
			},
			updateProductVersionId: function(url, productVersionId, namespace) {
				if (url.indexOf('patcherProductVersionId') === -1) {
					if (url.indexOf('?') === -1) {
						url += '?';
					}
					else {
						url += '&';
					}

					var newurl = url + namespace + 'patcherProductVersionId=' + productVersionId;
				}
				else {
					var re = /patcherProductVersionId=[0-9]*/;

					var newurl = url.replace(re, 'patcherProductVersionId=' + productVersionId);
				}
				return newurl;
			}
		}
	}
);